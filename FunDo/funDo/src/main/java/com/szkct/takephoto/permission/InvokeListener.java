package com.szkct.takephoto.permission;

//import org.devio.takephoto.model.InvokeParam;

import com.szkct.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
