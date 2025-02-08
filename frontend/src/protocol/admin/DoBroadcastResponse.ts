import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import Broadcast from './Broadcast';


class DoBroadcastResponse {
    broadcasts: Array<Broadcast> = [];
}

export class DoBroadcastResponseRegistration implements IProtocolRegistration<DoBroadcastResponse> {
    protocolId(): number {
        return 10004;
    }

    write(buffer: IByteBuffer, packet: DoBroadcastResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.broadcasts, 10002);
    }

    read(buffer: IByteBuffer): DoBroadcastResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new DoBroadcastResponse();
        const list0 = buffer.readPacketList(10002);
        packet.broadcasts = list0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default DoBroadcastResponse;