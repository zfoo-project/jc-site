import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class Broadcast {
    id: number = 0;
    content: string = '';
    weChatResult: string = '';
    smsResult: string = '';
}

export class BroadcastRegistration implements IProtocolRegistration<Broadcast> {
    protocolId(): number {
        return 10002;
    }

    write(buffer: IByteBuffer, packet: Broadcast | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.content);
        buffer.writeLong(packet.id);
        buffer.writeString(packet.smsResult);
        buffer.writeString(packet.weChatResult);
    }

    read(buffer: IByteBuffer): Broadcast | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new Broadcast();
        const result0 = buffer.readString();
        packet.content = result0;
        const result1 = buffer.readLong();
        packet.id = result1;
        const result2 = buffer.readString();
        packet.smsResult = result2;
        const result3 = buffer.readString();
        packet.weChatResult = result3;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default Broadcast;