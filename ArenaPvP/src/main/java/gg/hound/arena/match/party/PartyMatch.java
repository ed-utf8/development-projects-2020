package gg.hound.arena.match.party;

import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.user.party.Party;
import org.bukkit.Sound;

public class PartyMatch {

    private final Party partyOne;
    private final Party partyTwo;

    private final String partyOneLeaderName;
    private final String partyTwoLeaderName;

    private final Kit kit;

    private final boolean ranked;

    public int matchCountdown = 5;

    public PartyMatch(Party partyOne, Party partyTwo, String partyOneLeaderName, String partyTwoLeaderName, Kit kit, boolean ranked) {
        this.partyOne = partyOne;
        this.partyTwo = partyTwo;
        this.partyOneLeaderName = partyOneLeaderName;
        this.partyTwoLeaderName = partyTwoLeaderName;
        this.kit = kit;
        this.ranked = ranked;
    }

    public Party getPartyOne() {
        return partyOne;
    }

    public Party getPartyTwo() {
        return partyTwo;
    }

    public String getPartyOneLeaderName() {
        return partyOneLeaderName;
    }

    public String getPartyTwoLeaderName() {
        return partyTwoLeaderName;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isRanked() {
        return ranked;
    }

    public int getMatchCountdown() {
        return matchCountdown;
    }

    public void decrementMatchCountdown() {
        matchCountdown--;
    }
}
