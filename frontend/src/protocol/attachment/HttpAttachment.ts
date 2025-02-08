import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class HttpAttachment {
    uid: number = 0;
    taskExecutorHash: number = 0;
}

export class HttpAttachmentRegistration implements IProtocolRegistration<HttpAttachment> {
    protocolId(): number {
        return 4;
    }

    write(buffer: IByteBuffer, packet: HttpAttachment | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.taskExecutorHash);
        buffer.writeLong(packet.uid);
    }

    read(buffer: IByteBuffer): HttpAttachment | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new HttpAttachment();
        const result0 = buffer.readInt();
        packet.taskExecutorHash = result0;
        const result1 = buffer.readLong();
        packet.uid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default HttpAttachment;