package com.tregouet.knitting_images.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting_images.model.Reduction;

import io.realm.Realm;
import io.realm.Sort;

public class ReductionDao {

    private Realm mRealm;

    public ReductionDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public Reduction loadBy(int id) {
        Reduction reduction = mRealm.where(Reduction.class).equalTo("id", id).findFirst();
        if (reduction != null){
            return mRealm.copyFromRealm(reduction);
        } else {
            return null;
        }
    }

    public Reduction loadByStepId(int id) {
        Reduction reduction = mRealm.where(Reduction.class).equalTo("stepId", id).findFirst();
        if (reduction != null){
            return mRealm.copyFromRealm(reduction);
        } else {
            return null;
        }
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
