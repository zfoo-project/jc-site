import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class ChatgptForceStopResponse {
    requestId: number = 0;
}

export class ChatgptForceStopResponseRegistration implements IProtocolRegistration<ChatgptForceStopResponse> {
    protocolId(): number {
        return 503;
    }

    write(buffer: IByteBuffer, packet: ChatgptForceStopResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.requestId);
    }

    read(buffer: IByteBuffer): ChatgptForceStopResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ChatgptForceStopResponse();
        const result0 = buffer.readLong();
        packet.requestId = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ChatgptForceStopResponse;