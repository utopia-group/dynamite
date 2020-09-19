package dynamite;

public final class Config {

    // flag for debug output
    public static final boolean DEBUG = false;

    // flag for using MDP synthesizer
    // true for MDP synthesizer, false for enumeration synthesizer
    public static final boolean MDP_SYNTH = true;

    // maximum number of MDPs
    public static final int MAXIMUM_MDP_NUM = 3;

    // maximum number of copies for each direct collection
    public static final int MAXIMUM_COPY_NUM = 3;

    // synthesis timeout in seconds
    public static final int SYNTH_TIMEOUT = 3600;

}
