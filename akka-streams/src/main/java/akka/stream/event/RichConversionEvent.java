package akka.stream.event;

/**
 * @author by constantin on 6/27/17.
 */
public class RichConversionEvent extends RichCampaignEvent {
    final long pay;

    public RichConversionEvent(String rid, String cid, String pid, long timestamp, long pay) {
        super(rid, cid, pid, timestamp);
        this.pay = pay;
    }
}
