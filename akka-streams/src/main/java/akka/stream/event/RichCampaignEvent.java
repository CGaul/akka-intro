package akka.stream.event;

/**
 * @author by constantin on 6/27/17.
 */
public class RichCampaignEvent {
    public final String rid;
    public final String cid;
    public final String pid;
    public final Long timestamp;

    public RichCampaignEvent(String rid, String cid, String pid, Long timestamp) {
        this.rid = rid;
        this.cid = cid;
        this.pid = pid;
        this.timestamp = timestamp;
    }
}
