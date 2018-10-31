package com.tregouet.knitting.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting.model.Reduction;
import com.tregouet.knitting.utils.realm.RealmInt;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReductionDao {

    private Realm mRealm;

    public ReductionDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Reduction reduction) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(reduction);
            }
        });
    }

    public int nextId() {
        try {
            Reduction reduction = mRealm.where(Reduction.class).sort("id", Sort.DESCENDING).findAll().first();
            if (reduction != null) {
                System.out.println("nextId=" + reduction.getId());
                return (reduction.getId() + 1);
            } else {
                return 1;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
