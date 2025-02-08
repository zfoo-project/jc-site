import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class OssPolicyVO {
    policy: string = '';
    accessKeyId: string = '';
    signature: string = '';
    dir: string = '';
    host: string = '';
    expire: string = '';
}

export class OssPolicyVORegistration implements IProtocolRegistration<OssPolicyVO> {
    protocolId(): number {
        return 704;
    }

    write(buffer: IByteBuffer, packet: OssPolicyVO | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.accessKeyId);
        buffer.writeString(packet.dir);
        buffer.writeString(packet.expire);
        buffer.writeString(packet.host);
        buffer.writeString(packet.policy);
        buffer.writeString(packet.signature);
    }

    read(buffer: IByteBuffer): OssPolicyVO | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new OssPolicyVO();
        const result0 = buffer.readString();
        packet.accessKeyId = result0;
        const result1 = buffer.readString();
        packet.dir = result1;
        const result2 = buffer.readString();
        packet.expire = result2;
        const result3 = buffer.readString();
        packet.host = result3;
        const result4 = buffer.readString();
        packet.policy = result4;
        const result5 = buffer.readString();
        packet.signature = result5;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default OssPolicyVO;