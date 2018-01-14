package com.tregouet.tricot.utils;

import com.tregouet.tricot.dao.ProjectDao;
import com.tregouet.tricot.dao.RuleDao;
import com.tregouet.tricot.dao.StepDao;

import java.io.FileNotFoundException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmManager {

    private static Realm mRealm;

    public static Realm open() {
        try {
            mRealm = Realm.getDefaultInstance();
            return mRealm;
        } catch (RealmMigrationNeededException exception) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            try {
                Realm.migrateRealm(config);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return open();
        }

    }

    public static void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }

    public static ProjectDao createProjectDao() {
        checkForOpenRealm();
        return new ProjectDao(mRealm);
    }

    public static StepDao createStepDao() {
        checkForOpenRealm();
        return new StepDao(mRealm);
    }

    public static RuleDao createRuleDao() {
        checkForOpenRealm();
        return new RuleDao(mRealm);
    }

    public static void clear() {
        checkForOpenRealm();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
                //clear rest of your dao classes
            }
        });
    }

    private static void checkForOpenRealm() {
        if (mRealm == null) {
            throw new IllegalStateException("RealmManager: Realm is closed, call open() method first");
        } else if (mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
    }
}
