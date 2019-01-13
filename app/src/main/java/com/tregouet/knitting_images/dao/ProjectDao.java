package com.tregouet.knitting_images.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting_images.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;

public class ProjectDao {

    private Realm mRealm;

    public ProjectDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Project project) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(project);
            }
        });
    }

    public void save(final ArrayList<Project> projectsArrayList) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(projectsArrayList);
            }
        });
    }

    public List<Project> loadAll() {
        return mRealm.copyFromRealm(mRealm.where(Project.class).findAll());
    }


    public Project loadBy(int id) {
        Project project = mRealm.where(Project.class).equalTo("id", id).findFirst();
        if (project != null){
            return mRealm.copyFromRealm(project);
        } else {
            return null;
        }
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
        final RealmObject project = mRealm.where(Project.class).equalTo("id", id).findFirst();
        if (project != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    project.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Project project = mRealm.where(Project.class).sort("id", Sort.DESCENDING).findAll().first();
            if (project != null) {
                int index = project.getId() + 1;
                return index;
            } else {
                return 0;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return  0;
        }
    }
}
