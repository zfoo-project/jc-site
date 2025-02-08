import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class Heartbeat {
    
}

export class HeartbeatRegistration implements IProtocolRegistration<Heartbeat> {
    protocolId(): number {
        return 102;
    }

    write(buffer: IByteBuffer, packet: Heartbeat | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
    }

    read(buffer: IByteBuffer): Heartbeat | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new Heartbeat();
        
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default Heartbeat;