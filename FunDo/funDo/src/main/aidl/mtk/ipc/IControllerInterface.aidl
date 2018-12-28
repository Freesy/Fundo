package mtk.ipc;

import mtk.ipc.IControllerCallback;

interface IControllerInterface {

    /**
     * Init IPCController.
     * @param cmd_type Only support Wearable SDK Controller CMD_8 or CMD_9
     * @param tagName Controller Tag
     */
    int init(int cmd_type, in String tagName);

    /**
     * Send bytes to Wearable.
     * @param tagName Controller Tag, use your IPCController init tag.
     * @param cmd command string, like "yahooweather yahooweather 1 0 0 "
     * @param dataBuffer byte type of data, like "".getBytes()
     * @param priority default PRIORITY_NORMAL, if set as PRIORITY_HIGH, this session
     *        will get top priority to send.
     */
    long sendBytes(in String tagName, String cmd, in byte[] data, int priority);

    /**
     * Return connection state.
     * 
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
    int getConnectionState();

    /**
     * Destroy the IPCController.
     */
    void close(String tagName);

    /**
     * register IControllerCallback for the "tagName" IPCController.
     */
    void registerControllerCallback(in String tagName, in IControllerCallback callback);

    /**
     * unregister IControllerCallback for the "tagName" IPCController.
     */
    void unregisterControllerCallback(in String tagName, in IControllerCallback callback);

    /**
     * get SmartDevice APK remote Bluetooth device Name.
     */
    String getRemoteDeviceName();
}
