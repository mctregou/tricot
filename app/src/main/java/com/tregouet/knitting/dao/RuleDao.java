package com.tregouet.knitting.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting.model.Rule;
import com.tregouet.knitting.model.Rule;
import com.tregouet.knitting.utils.realm.RealmInt;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
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
            public void execute(Realm realm) {
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


    public Rule loadBy(int id) {
        return mRealm.where(Rule.class).equalTo("id", id).findFirst();
    }

    public List<Rule> loadByStepId(int id) {
        return mRealm.copyFromRealm(mRealm.where(Rule.class).equalTo("stepId", id).findAll());
    }

    public ArrayList<Rule> loadByIds(List<RealmInt> ids) {
        ArrayList<Rule> stitches = new ArrayList<>();
        for (RealmInt stitchId : ids){
            Rule stitch = mRealm.where(Rule.class).equalTo("id", stitchId.getVal()).findFirst();
            if (stitch != null) {
                stitches.add(mRealm.copyFromRealm(stitch));
            }
        }
        return stitches;
    }


    public void remove(@NonNull final RealmObject object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                object.deleteFromRealm();
            }
        });
    }

    public void removeById(int id) {
        final RealmObject rule = mRealm.where(Rule.class).equalTo("id", id).findFirst();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rule.deleteFromRealm();
                }
            });
        }
    }

    public void removeByProjectId(int id) {
        final RealmResults<Rule> rule = mRealm.where(Rule.class).equalTo("projectId", id).findAll();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rule.deleteAllFromRealm();
                }
            });
        }
    }

    public void removeByStepId(int id) {
        final RealmResults<Rule> rule = mRealm.where(Rule.class).equalTo("stepId", id).findAll();
        if (rule != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rule.deleteAllFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Rule rule = mRealm.where(Rule.class).sort("id", Sort.DESCENDING).findAll().first();
            if (rule != null) {
                System.out.println("nextId=" + rule.getId());
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
