package com.tregouet.knitting.utils.realm;

import io.realm.RealmObject;

/**
 * Created by mariececile.tregouet on 10/09/2018.
 */

public class RealmInt extends RealmObject {
    private int val;

    public RealmInt() {
    }

    public RealmInt(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
