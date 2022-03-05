package com.tregouet.tricot.dao;

import androidx.annotation.NonNull;

import com.tregouet.tricot.model.ReductionItem;
import com.tregouet.tricot.utils.Constants;

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
        return mRealm.copyFromRealm(mRealm.where(ReductionItem.class).equalTo(Constants.TABLE_REDUCTION_ITEM_REDUCTION_ID, id).findAll());
    }

    public void save(final ReductionItem reductionItem) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(reductionItem);
            }
        });
    }

    public void removeByReductionId(int id) {
        final RealmResults<ReductionItem> reductionItems = mRealm.where(ReductionItem.class).equalTo(Constants.TABLE_REDUCTION_ITEM_REDUCTION_ID, id).findAll();
        if (reductionItems != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    reductionItems.deleteAllFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            ReductionItem reductionItem = mRealm.where(ReductionItem.class).sort(Constants.TABLE_REDUCTION_ITEM_ID, Sort.DESCENDING).findAll().first();
            if (reductionItem != null && reductionItem.getId() != null) {
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
