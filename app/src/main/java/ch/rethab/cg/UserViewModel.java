package ch.rethab.cg;

import android.arch.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private int crushes;

    private String playerName = "foo";

    public void setCrushes(int crushes) {
        this.crushes = crushes;
    }

    public int getCrushes() {
        return crushes;
    }

    public String getPlayerName() {
        return playerName;
    }
}
