import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class ChatgptMessageNotice {
    requestId: number = 0;
    chatAI: number = 0;
    choice: string = '';
    finishReason: number = 0;
}

export class ChatgptMessageNoticeRegistration implements IProtocolRegistration<ChatgptMessageNotice> {
    protocolId(): number {
        return 501;
    }

    write(buffer: IByteBuffer, packet: ChatgptMessageNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.chatAI);
        buffer.writeString(packet.choice);
        buffer.writeInt(packet.finishReason);
        buffer.writeLong(packet.requestId);
    }

    read(buffer: IByteBuffer): ChatgptMessageNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ChatgptMessageNotice();
        const result0 = buffer.readInt();
        packet.chatAI = result0;
        const result1 = buffer.readString();
        packet.choice = result1;
        const result2 = buffer.readInt();
        packet.finishReason = result2;
        const result3 = buffer.readLong();
        packet.requestId = result3;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ChatgptMessageNotice;