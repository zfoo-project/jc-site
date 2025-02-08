import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import SdSimulateNotice from './SdSimulateNotice';


class TransferSdSimulateNotify {
    noticeSid: number = 0;
    notice: SdSimulateNotice | null = null;
}

export class TransferSdSimulateNotifyRegistration implements IProtocolRegistration<TransferSdSimulateNotify> {
    protocolId(): number {
        return 1072;
    }

    write(buffer: IByteBuffer, packet: TransferSdSimulateNotify | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.notice, 1051);
        buffer.writeLong(packet.noticeSid);
    }

    read(buffer: IByteBuffer): TransferSdSimulateNotify | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferSdSimulateNotify();
        const result0 = buffer.readPacket(1051);
        packet.notice = result0;
        const result1 = buffer.readLong();
        packet.noticeSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferSdSimulateNotify;