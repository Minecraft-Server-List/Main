package com.example.minecraft.dto;

public class ServerStatusDTO {
    public static class Players {
        public int online;
        public int max;
    }

    public boolean online;
    public Players players;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }
}