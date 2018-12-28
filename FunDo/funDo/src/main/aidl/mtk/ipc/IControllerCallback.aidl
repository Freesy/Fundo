package mtk.ipc;

interface IControllerCallback {

    /**
     * Notify WearableManager connection state.
     *
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
    void onConnectionStateChange(int state);

    /**
     * Notify WearableManager received data.
     */
    void onBytesReceived(in byte[] dataBuffer);
}
