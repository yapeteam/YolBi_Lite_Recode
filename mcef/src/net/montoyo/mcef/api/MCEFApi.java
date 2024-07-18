package net.montoyo.mcef.api;

import net.montoyo.mcef.MCEF;

public class MCEFApi {

    /**
     * Call this to get the API instance.
     *
     * @return the MCEF API or null if something failed.
     */
    public static API getAPI() {
        return MCEF.PROXY;
    }

    /**
     * Checks if MCEF was loaded by forge.
     *
     * @return true if it is loaded. false otherwise.
     */
    public static boolean isMCEFLoaded() {
        return true;
    }
}
