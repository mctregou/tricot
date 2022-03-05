package com.tregouet.tricot.dao;

import androidx.annotation.NonNull;

import com.tregouet.tricot.model.Image;
import com.tregouet.tricot.utils.Constants;

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
            public void execute(@NonNull Realm realm) {
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
        return mRealm.copyFromRealm(mRealm.where(Image.class).equalTo(Constants.TABLE_IMAGE_TYPE, type).equalTo(Constants.TABLE_IMAGE_ELEMENT_ID, elementId).findAll());
    }

    public void removeById(int id) {
        final RealmObject image = mRealm.where(Image.class).equalTo(Constants.TABLE_IMAGE_ID, id).findFirst();
        if (image != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    image.deleteFromRealm();
                }
            });
        }
    }

    public int nextId() {
        try {
            Image image = mRealm.where(Image.class).sort(Constants.TABLE_IMAGE_ID, Sort.DESCENDING).findAll().first();
            if (image != null && image.getId() != null) {
                return image.getId() + 1;
            } else {
                return 0;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            return  0;
        }
    }
}
