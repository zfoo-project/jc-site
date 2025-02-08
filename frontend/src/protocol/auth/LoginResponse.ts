import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import User from '../user/User';


class LoginResponse {
    ip: string = '';
    ipLong: number = 0;
    sid: number = 0;
    activeUid: number = 0;
    region: string = '';
    chatMessageIdDiff: number = 0;
    user: User | null = null;
}

export class LoginResponseRegistration implements IProtocolRegistration<LoginResponse> {
    protocolId(): number {
        return 701;
    }

    write(buffer: IByteBuffer, packet: LoginResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.activeUid);
        buffer.writeLong(packet.chatMessageIdDiff);
        buffer.writeString(packet.ip);
        buffer.writeLong(packet.ipLong);
        buffer.writeString(packet.region);
        buffer.writeLong(packet.sid);
        buffer.writePacket(packet.user, 750);
    }

    read(buffer: IByteBuffer): LoginResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new LoginResponse();
        const result0 = buffer.readLong();
        packet.activeUid = result0;
        const result1 = buffer.readLong();
        packet.chatMessageIdDiff = result1;
        const result2 = buffer.readString();
        packet.ip = result2;
        const result3 = buffer.readLong();
        packet.ipLong = result3;
        const result4 = buffer.readString();
        packet.region = result4;
        const result5 = buffer.readLong();
        packet.sid = result5;
        const result6 = buffer.readPacket(750);
        packet.user = result6;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default LoginResponse;