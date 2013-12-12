package com.lgerenu.lurraldebus;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class GeltokienZerrendaParcelabe extends ArrayList<Geltokia> implements
		Parcelable {

	public GeltokienZerrendaParcelabe() {
	}

	public void writeToParcel(Parcel dest, int flags) {
		int size = this.size();
		dest.writeInt(size);
		for (int i = 0; i < size; i++) {
			Geltokia Geltokia = this.get(i);
			dest.writeInt(Geltokia.getId());
			dest.writeString(Geltokia.getName());
			dest.writeString(Geltokia.getDesc());
			dest.writeDouble(Geltokia.getLat());
			dest.writeDouble(Geltokia.getLon());
			dest.writeInt(Geltokia.getDistantzia());
		}
	}

	public GeltokienZerrendaParcelabe(Parcel in) {
		readfromParcel(in);
	}

	private void readfromParcel(Parcel in) {
		this.clear();
		// Leemos el tamaño del array
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			// el orden de los atributos SI importa
			Geltokia geltokia = new Geltokia();
			geltokia.setId(in.readInt());
			geltokia.setName(in.readString());
			geltokia.setDesc(in.readString());
			geltokia.setLat(in.readDouble());
			geltokia.setLon(in.readDouble());
			geltokia.setDistantzia(in.readInt());
			this.add(geltokia);
		}
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public GeltokienZerrendaParcelabe createFromParcel(Parcel in) {
			return new GeltokienZerrendaParcelabe(in);
		}

		public Object[] newArray(int arg0) {
			return null;
		}
	};

	public int describeContents() {
		return 0;
	}
}
