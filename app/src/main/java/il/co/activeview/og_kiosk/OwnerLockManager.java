package il.co.activeview.og_kiosk;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.UserManager;
import android.provider.Settings;

public class OwnerLockManager {
    private Activity context;

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    private PackageManager mPackageManager;

    public  OwnerLockManager(Activity context){
        mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = context.getPackageManager();
        this.context = context;
    }

    public boolean initLockAdmin() {
        // Set Default COSU policy

        if(mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())){
            setDefaultCosuPolicies(true);
            return true;
        }
        else {

        }
        return false;
    }
    public boolean startLock() {
        if(mDevicePolicyManager.isLockTaskPermitted(context.getPackageName())){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                 context.startLockTask();

            }
            return true;

        }
        return false;

    }
    public boolean stopLock(){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        if (isLocked()) {
            context.stopLockTask();
            setDefaultCosuPolicies(false);
            return true;
        }
        setDefaultCosuPolicies(false);

        return false;
    }
    public boolean isLocked(){
        if(isAdminLockPermitted()){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdminLockPermitted(){
        return mDevicePolicyManager.isLockTaskPermitted(context.getPackageName());
    }

    private void setDefaultCosuPolicies(boolean active){
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // set system update policy
        if (active){
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, active ? new String[]{context.getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(mAdminComponentName, intentFilter, new ComponentName(context.getPackageName(), KioskActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, context.getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow){
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName, restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled){
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(mAdminComponentName, Settings.Global.STAY_ON_WHILE_PLUGGED_IN,"0" );
        }
    }

}

