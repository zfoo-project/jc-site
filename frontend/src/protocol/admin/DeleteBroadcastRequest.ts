import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class DeleteBroadcastRequest {
    id: number = 0;
}

export class DeleteBroadcastRequestRegistration implements IProtocolRegistration<DeleteBroadcastRequest> {
    protocolId(): number {
        return 10010;
    }

    write(buffer: IByteBuffer, packet: DeleteBroadcastRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.id);
    }

    read(buffer: IByteBuffer): DeleteBroadcastRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new DeleteBroadcastRequest();
        const result0 = buffer.readLong();
        packet.id = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default DeleteBroadcastRequest;