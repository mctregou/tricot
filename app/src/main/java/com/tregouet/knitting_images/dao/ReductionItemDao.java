package com.tregouet.knitting_images.dao;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tregouet.knitting_images.model.ReductionItem;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReductionItemDao {

    private Realm mRealm;

    public ReductionItemDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public List<ReductionItem> loadByReductionId(int id) {
        return mRealm.copyFromRealm(mRealm.where(ReductionItem.class).equalTo("reductionId", id).findAll());
    }

    public void save(final ReductionItem reductionItem) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(reductionItem);
            }
        });
    }

    public void removeByReductionId(int id) {
        final RealmResults<ReductionItem> reductionItems = mRealm.where(ReductionItem.class).equalTo("reductionId", id).findAll();
        if (reductionItems != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    reductionItems.deleteAllFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            ReductionItem reductionItem = mRealm.where(ReductionItem.class).sort("id", Sort.DESCENDING).findAll().first();
            if (reductionItem != null) {
                Log.i("ReductionDao", "nextId=" + reductionItem.getId());
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
