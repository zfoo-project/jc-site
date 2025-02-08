import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import AnimationNotice from './AnimationNotice';


class TransferAnimationNotify {
    requestSid: number = 0;
    notice: AnimationNotice | null = null;
}

export class TransferAnimationNotifyRegistration implements IProtocolRegistration<TransferAnimationNotify> {
    protocolId(): number {
        return 1214;
    }

    write(buffer: IByteBuffer, packet: TransferAnimationNotify | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.notice, 1212);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferAnimationNotify | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferAnimationNotify();
        const result0 = buffer.readPacket(1212);
        packet.notice = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferAnimationNotify;