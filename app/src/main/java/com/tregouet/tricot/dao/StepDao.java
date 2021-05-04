package com.tregouet.tricot.dao;

import android.support.annotation.NonNull;

import com.tregouet.tricot.model.Step;
import com.tregouet.tricot.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class StepDao {

    private Realm mRealm;

    public StepDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Step step) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
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
        Step step = mRealm.where(Step.class).equalTo(Constants.TABLE_STEP_ID, id).findFirst();
        if (step != null){
            return mRealm.copyFromRealm(step);
        } else {
            return null;
        }
    }

    public List<Step> loadByProjectId(int id) {
        return mRealm.copyFromRealm(mRealm.where(Step.class).equalTo(Constants.TABLE_STEP_PROJECT_ID, id).findAll());
    }

    public void removeByProjectId(int id) {
        final RealmResults<Step> step = mRealm.where(Step.class).equalTo(Constants.TABLE_STEP_PROJECT_ID, id).findAll();
        if (step != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    step.deleteAllFromRealm();
                }
            });
        }
    }

    public void removeById(int id) {
        final RealmObject step = mRealm.where(Step.class).equalTo(Constants.TABLE_STEP_ID, id).findFirst();
        if (step != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    step.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Step step = mRealm.where(Step.class).sort(Constants.TABLE_STEP_ID, Sort.DESCENDING).findAll().first();
            if (step != null && step.getId() != null) {
                return (step.getId() + 1);
            } else {
                return 0;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return 0;
        }
    }

    public Step loadLastStep() {
        List<Step> steps = mRealm.where(Step.class).sort(Constants.TABLE_STEP_LAST_SEEN_FIELD, Sort.DESCENDING).findAll();
        if (steps != null && steps.size() > 0) {
            return mRealm.copyFromRealm(steps.get(0));
        } else {
            return null;
        }
    }
}
