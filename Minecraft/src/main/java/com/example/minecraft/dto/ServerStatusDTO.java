package com.example.minecraft.dto;

public class ServerStatusDTO {

    private boolean online;
    private PlayersDTO players;
    private VersionDTO version;

    public ServerStatusDTO() {}

    public boolean getOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public PlayersDTO getPlayers() { return players; }
    public void setPlayers(PlayersDTO players) { this.players = players; }

    public VersionDTO getVersion() { return version; }
    public void setVersion(VersionDTO version) { this.version = version; }
}