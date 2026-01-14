package com.sleeve.nettyrpc.provider;

public class ProviderApp {

    public static void main(String[] args) {
        ProviderServer providerServer = new ProviderServer(8888);
        providerServer.start();
    }
}
