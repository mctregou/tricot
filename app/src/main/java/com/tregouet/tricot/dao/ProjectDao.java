package com.tregouet.tricot.dao;

import androidx.annotation.NonNull;

import com.tregouet.tricot.model.Project;
import com.tregouet.tricot.utils.Constants;

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
            public void execute(@NonNull Realm realm) {
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
        Project project = mRealm.where(Project.class).equalTo(Constants.TABLE_PROJECT_ID, id).findFirst();
        if (project != null){
            return mRealm.copyFromRealm(project);
        } else {
            return null;
        }
    }

    public Project loadByName(String name) {
        Project project = mRealm.where(Project.class).equalTo(Constants.TABLE_PROJECT_NAME, name).findFirst();
        if (project != null){
            return mRealm.copyFromRealm(project);
        } else {
            return null;
        }
    }

    public void removeById(int id) {
        final RealmObject project = mRealm.where(Project.class).equalTo(Constants.TABLE_PROJECT_ID, id).findFirst();
        if (project != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    project.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Project project = mRealm.where(Project.class).sort(Constants.TABLE_PROJECT_ID, Sort.DESCENDING).findAll().first();
            if (project != null && project.getId() != null) {
                return project.getId() + 1;
            } else {
                return 0;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return  0;
        }
    }
}
