package com.tregouet.tricot.dao;

import androidx.annotation.NonNull;

import com.tregouet.tricot.model.Rule;
import com.tregouet.tricot.utils.Constants;
import com.tregouet.tricot.utils.realm.RealmInt;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class RuleDao {

    private Realm mRealm;

    public RuleDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Rule rule) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(rule);
            }
        });
    }

    public void save(final ArrayList<Rule> rulesArrayList) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(rulesArrayList);
            }
        });
    }

    public List<Rule> loadAll() {
        return mRealm.copyFromRealm(mRealm.where(Rule.class).findAll());
    }


    public List<Rule> loadByStepId(int id) {
        return mRealm.copyFromRealm(mRealm.where(Rule.class).equalTo(Constants.TABLE_RULE_STEP_ID, id).findAll());
    }

    public ArrayList<Rule> loadByIds(List<RealmInt> ids) {
        ArrayList<Rule> stitches = new ArrayList<>();
        for (RealmInt stitchId : ids){
            Rule stitch = mRealm.where(Rule.class).equalTo(Constants.TABLE_RULE_ID, stitchId.getVal()).findFirst();
            if (stitch != null) {
                stitches.add(mRealm.copyFromRealm(stitch));
            }
        }
        return stitches;
    }

    public void removeAllById(List<RealmInt> ids) {
        for (RealmInt stitchId : ids){
            removeById(stitchId.getVal());
        }
    }


    public void removeById(int id) {
        final RealmObject rule = mRealm.where(Rule.class).equalTo(Constants.TABLE_RULE_ID, id).findFirst();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    rule.deleteFromRealm();
                }
            });
        }
    }

    public void removeByProjectId(int id) {
        final RealmResults<Rule> rule = mRealm.where(Rule.class).equalTo(Constants.TABLE_RULE_PROJECT_ID, id).findAll();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    rule.deleteAllFromRealm();
                }
            });
        }
    }

    public void removeByStepId(int id) {
        final RealmResults<Rule> rule = mRealm.where(Rule.class).equalTo(Constants.TABLE_RULE_STEP_ID, id).findAll();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    rule.deleteAllFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Rule rule = mRealm.where(Rule.class).sort(Constants.TABLE_RULE_ID, Sort.DESCENDING).findAll().first();
            if (rule != null && rule.getId() != null) {
                return (rule.getId() + 1);
            } else {
                return 1;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
