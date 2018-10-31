package com.tregouet.knitting.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting.model.ReductionItem;

import io.realm.Realm;
import io.realm.Sort;

public class ReductionItemDao {

    private Realm mRealm;

    public ReductionItemDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final ReductionItem reductionItem) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(reductionItem);
            }
        });
    }

    public int nextId() {
        try {
            ReductionItem reductionItem = mRealm.where(ReductionItem.class).sort("id", Sort.DESCENDING).findAll().first();
            if (reductionItem != null) {
                System.out.println("nextId=" + reductionItem.getId());
                return (reductionItem.getId() + 1);
            } else {
                return 1;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
