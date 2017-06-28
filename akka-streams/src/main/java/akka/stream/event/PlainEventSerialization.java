package akka.stream.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author by constantin on 6/28/17.
 */
public class PlainEventSerialization {

    public static<T> byte[] serialize(T event) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(event);
        return bos.toByteArray();
    }

    public static<T> T deserialize(byte[] value) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(value);
        ObjectInputStream ois = new ObjectInputStream(bis);
        T event = (T) ois.readObject();
        bis.close();
        ois.close();
        return event;
    }
}
