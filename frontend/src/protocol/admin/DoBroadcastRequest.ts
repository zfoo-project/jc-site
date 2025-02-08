import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class DoBroadcastRequest {
    id: number = 0;
    // sms代表短信通知，wechat代表微信通知
    type: string = '';
}

export class DoBroadcastRequestRegistration implements IProtocolRegistration<DoBroadcastRequest> {
    protocolId(): number {
        return 10003;
    }

    write(buffer: IByteBuffer, packet: DoBroadcastRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.id);
        buffer.writeString(packet.type);
    }

    read(buffer: IByteBuffer): DoBroadcastRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new DoBroadcastRequest();
        const result0 = buffer.readLong();
        packet.id = result0;
        const result1 = buffer.readString();
        packet.type = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default DoBroadcastRequest;