import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class ChatMessage {
    id: number = 0;
    type: number = 0;
    sendId: number = 0;
    region: string = '';
    message: string = '';
    timestamp: number = 0;
}

export class ChatMessageRegistration implements IProtocolRegistration<ChatMessage> {
    protocolId(): number {
        return 600;
    }

    write(buffer: IByteBuffer, packet: ChatMessage | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.id);
        buffer.writeString(packet.message);
        buffer.writeString(packet.region);
        buffer.writeLong(packet.sendId);
        buffer.writeLong(packet.timestamp);
        buffer.writeByte(packet.type);
    }

    read(buffer: IByteBuffer): ChatMessage | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ChatMessage();
        const result0 = buffer.readLong();
        packet.id = result0;
        const result1 = buffer.readString();
        packet.message = result1;
        const result2 = buffer.readString();
        packet.region = result2;
        const result3 = buffer.readLong();
        packet.sendId = result3;
        const result4 = buffer.readLong();
        packet.timestamp = result4;
        const result5 = buffer.readByte();
        packet.type = result5;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ChatMessage;