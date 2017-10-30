package de.hska.iwi.bdelab.batchstore;

import de.hska.iwi.bdelab.schema.Data;
import de.hska.iwi.bdelab.schema.DataUnit;
import de.hska.iwi.bdelab.schema.FriendEdge;
import de.hska.iwi.bdelab.schema.Pedigree;
import de.hska.iwi.bdelab.schema.UserID;
import de.hska.iwi.bdelab.schema.UserProperty;
import de.hska.iwi.bdelab.schema.UserPropertyValue;
import java.lang.Math;

public class FriendFacts {

	private static UserID uid1 = new UserID();
	private static UserID uid2 = new UserID();
	private static UserPropertyValue upv1 = new UserPropertyValue();
	private static UserPropertyValue upv2 = new UserPropertyValue();
	private static UserProperty up1 = new UserProperty();
	private static UserProperty up2 = new UserProperty();
	private static FriendEdge fe1 = new FriendEdge();
	private static DataUnit du1 = new DataUnit();
	private static DataUnit du2 = new DataUnit();
	private static DataUnit du3 = new DataUnit();
	private static Pedigree p1 = new Pedigree();
	private static Pedigree p2 = new Pedigree();
	private static Pedigree p3 = new Pedigree();

	public static Data d1 = new Data();
	public static Data d2 = new Data();
	public static Data d3 = new Data();

	static {
		uid1.set_user_id("123");
		uid2.set_user_id("321");
		upv1.set_full_name("alice");
		upv2.set_full_name("bob");
		up1.set_property(upv1);
		up1.set_id(uid1);
		up2.set_property(upv2);
		up2.set_id(uid2);
		fe1.set_id1(uid1);
		fe1.set_id2(uid2);
		du1.set_user_property(up1);
		du2.set_user_property(up2);
		du3.set_friend(fe1);
		p1.set_true_as_of_secs((int)(System.currentTimeMillis() / 1000));
		p2.set_true_as_of_secs((int)(System.currentTimeMillis() / 1000));
		p3.set_true_as_of_secs((int)(System.currentTimeMillis() / 1000));
		d1.set_dataunit(du1);
		d1.set_pedigree(p1);
		d2.set_dataunit(du2);
		d2.set_pedigree(p2);
		d3.set_dataunit(du3);
		d3.set_pedigree(p3);
	}

}
