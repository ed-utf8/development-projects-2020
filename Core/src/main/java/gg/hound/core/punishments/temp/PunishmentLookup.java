package gg.hound.core.punishments.temp;

import java.util.ArrayList;

public class PunishmentLookup {

    private final ArrayList<TempPunishment> tempPunishmentArrayList;

    public PunishmentLookup(ArrayList<TempPunishment> tempPunishmentArrayList) {
        this.tempPunishmentArrayList = tempPunishmentArrayList;
    }

    public ArrayList<TempPunishment> getTempPunishmentArrayList() {
        return tempPunishmentArrayList;
    }
}
