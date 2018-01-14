package com.tregouet.tricot.dao;

import android.support.annotation.NonNull;

import com.tregouet.tricot.model.Rule;
import com.tregouet.tricot.model.Step;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;

public class StepDao {

    private Realm mRealm;

    public StepDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Step step) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(step);
            }
        });
    }

    public void save(final ArrayList<Step> stepsArrayList) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(stepsArrayList);
            }
        });
    }

    public List<Step> loadAll() {
        return mRealm.copyFromRealm(mRealm.where(Step.class).findAll());
    }


    public Step loadBy(int id) {
        Step step = mRealm.where(Step.class).equalTo("id", id).findFirst();
        if (step != null){
            return mRealm.copyFromRealm(step);
        } else {
            return null;
        }
    }

    public List<Step> loadByProjectId(int id) {
        return mRealm.copyFromRealm(mRealm.where(Step.class).equalTo("projectId", id).findAll());
    }

    public void remove(@NonNull final RealmObject object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                object.deleteFromRealm();
            }
        });
    }

    public void removeByProjectId(int id) {
        final RealmResults<Step> step = mRealm.where(Step.class).equalTo("projectId", id).findAll();
        if (step != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    step.deleteAllFromRealm();
                }
            });
        }
    }

    public void removeById(int id) {
        final RealmObject step = mRealm.where(Step.class).equalTo("id", id).findFirst();
        if (step != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    step.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Step step = mRealm.where(Step.class).sort("id", Sort.DESCENDING).findAll().first();
            if (step != null) {
                return (step.getId() + 1);
            } else {
                return 0;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return 0;
        }
    }
}
