package com.tregouet.knitting_images.dao;

import android.support.annotation.NonNull;

import com.tregouet.knitting_images.model.Image;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;

public class ImageDao {

    private Realm mRealm;

    public ImageDao(@NonNull Realm realm) {
        mRealm = realm;
    }

    public void save(final Image image) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(image);
            }
        });
    }

    public void save(final ArrayList<Image> imagesArrayList) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(imagesArrayList);
            }
        });
    }

    public List<Image> loadAllForElement(final int type, final int elementId) {
        return mRealm.copyFromRealm(mRealm.where(Image.class).equalTo("type", type).equalTo("elementId", elementId).findAll());
    }


    public Image loadBy(int id) {
        Image image = mRealm.where(Image.class).equalTo("id", id).findFirst();
        if (image != null){
            return mRealm.copyFromRealm(image);
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
        final RealmObject image = mRealm.where(Image.class).equalTo("id", id).findFirst();
        if (image != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    image.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Image image = mRealm.where(Image.class).sort("id", Sort.DESCENDING).findAll().first();
            if (image != null) {
                int index = image.getId() + 1;
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
