import {onCall, HttpsError} from "firebase-functions/v2/https";
import {defineSecret} from "firebase-functions/params";
import {getFirestore, FieldValue} from "firebase-admin/firestore";
import {initializeApp} from "firebase-admin/app";

initializeApp();

const geminiApiKey = defineSecret("GEMINI_API_KEY");

const DAILY_LIMIT_FREE = 20;

interface GeminiContent {
  role: string;
  parts: {text: string}[];
}

interface ChatRequest {
  contents: GeminiContent[];
  systemInstruction?: GeminiContent;
}

export const chat = onCall(
  {secrets: [geminiApiKey], memory: "256MiB", timeoutSeconds: 60},
  async (request) => {
    // 1. Auth check
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Sign in required");
    }
    const uid = request.auth.uid;

    // 2. Rate limit check
    const db = getFirestore();
    const today = new Date().toISOString().slice(0, 10); // "2026-04-07"
    const limitsRef = db.doc(`rateLimits/${uid}/daily/${today}`);

    const limitsSnap = await limitsRef.get();
    const currentCount = limitsSnap.exists ? (limitsSnap.data()?.count ?? 0) : 0;

    if (currentCount >= DAILY_LIMIT_FREE) {
      throw new HttpsError(
        "resource-exhausted",
        `Daily limit reached (${DAILY_LIMIT_FREE} messages/day)`
      );
    }

    // 3. Build Gemini request
    const {contents, systemInstruction} = request.data as ChatRequest;

    if (!contents || !Array.isArray(contents) || contents.length === 0) {
      throw new HttpsError("invalid-argument", "contents is required");
    }

    const geminiBody = {
      contents,
      systemInstruction,
      generationConfig: {
        responseMimeType: "application/json",
      },
    };

    // 4. Call Gemini API
    const url =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent" +
      `?key=${geminiApiKey.value()}`;

    const resp = await fetch(url, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(geminiBody),
    });

    if (!resp.ok) {
      const errorText = await resp.text();
      console.error("Gemini API error:", resp.status, errorText);
      throw new HttpsError("internal", "AI service error");
    }

    const geminiResponse = await resp.json();

    // 5. Increment counter
    await limitsRef.set(
      {count: FieldValue.increment(1), updatedAt: FieldValue.serverTimestamp()},
      {merge: true}
    );

    // 6. Return the AI response text
    const text =
      geminiResponse?.candidates?.[0]?.content?.parts?.[0]?.text ?? "";

    return {text};
  }
);
