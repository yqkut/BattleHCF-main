package cc.stormworth.hcf.misc.payout;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import cc.stormworth.hcf.Main;
import lombok.Getter;

public final class PayoutManager {

	private static final ReplaceOptions MONGO_REPLACE_OPTIONS = new ReplaceOptions().upsert(true);
	
	@Getter
	private final List<Payout> payouts;
	private final MongoCollection<Document> payoutsCollection = Main.getInstance().getMongoPool().getDatabase(Main.DATABASE_NAME).getCollection("Payouts");
	
	public PayoutManager() {
		this.payouts = this.load();
	}
	
	private List<Payout> load() {
		List<Payout> payouts = Lists.newArrayList();
		
		try (MongoCursor<Document> cursor = this.payoutsCollection.find().iterator()) {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				
				payouts.add(
						new Payout(
								document.getString("_id"),
								document.getString("type"),
								document.getString("faction"),
								document.getString("faction_leader"),
								document.getString("created_on"),
								document.getString("payed_on")
								)
						);
			}
		}
		
		return payouts;
	}
	
	public void savePayout(Payout payout) {
		Document document = new Document();
		
		document.put("_id", payout.getId());
		document.put("type", payout.getType());
		document.put("faction", payout.getFaction());
		document.put("faction_leader", payout.getFactionLeader());
		document.put("created_on", payout.getCreatedOn());
		document.put("payed_on", payout.getPayedOn());
		
		this.payoutsCollection.replaceOne(Filters.eq("_id", payout.getId()), document, MONGO_REPLACE_OPTIONS);
	}
	
	public void deletePayout(Payout payout) {
		this.payoutsCollection.deleteOne(Filters.eq("_id", payout.getId()));
	}
}
