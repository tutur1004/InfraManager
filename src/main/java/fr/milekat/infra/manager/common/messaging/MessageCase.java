package fr.milekat.infra.manager.common.messaging;

/**
 * Javadoc list arguments from message, started at index 2
 */
public enum MessageCase {
    //  From Proxy To Host
    INVITE_SENT,                // TODO : TEST
    INVITE_RESULT_NOT_FOUND,    // TODO : TEST
    INVITE_RESULT_DENY,         // TODO

    //  From Host To Proxy
    /**
     * Empty
     */
    GAME_READY,                 // TODO : TEST
    /**
     * Empty
     */
    GAME_FINISHED,              // TODO : TEST
    /**
     * Empty
     */
    HOST_JOINED,                // TODO : TEST

    /**
     * 2: Uuid of invited player
     */
    HOST_INVITE_PLAYER,         // TODO : TEST then improve
    /**
     * 2: Uuid of cancelled player
     */
    HOST_DENIED_REQUEST,        // TODO : TEST then improve maybe ?

    //  From Lobby To Host
    JOIN_REQUEST,               // Ignored by PROXY

    //  From Lobby To Proxy
    /**
     * 2: Uuid of player<br>
     * 3: GameId<br>
     */
    ASK_CREATE_HOST,            // TODO : TEST

    //  From Proxy To Lobby
    /**
     * 2: Uuid of player<br>
     */
    CREATE_HOST_FAILED,         // TODO : TEST
}
