package akka.stream.event;

/**
 * @author by constantin on 6/27/17.
 */
public class RichClickEvent extends RichCampaignEvent{

    public RichClickEvent(String rid, String cid, String pid, Long timestamp) {
        super(rid, cid, pid, timestamp);
    }
}
