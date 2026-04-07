package com.pimenov.crm.core.domain.reminder

interface ReminderPermissionChecker {
    fun needsPermission(): Boolean
}
