import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import User from './User';


class UserProfileNotice {
    user: User | null = null;
    token: string = '';
}

export class UserProfileNoticeRegistration implements IProtocolRegistration<UserProfileNotice> {
    protocolId(): number {
        return 751;
    }

    write(buffer: IByteBuffer, packet: UserProfileNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.token);
        buffer.writePacket(packet.user, 750);
    }

    read(buffer: IByteBuffer): UserProfileNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new UserProfileNotice();
        const result0 = buffer.readString();
        packet.token = result0;
        const result1 = buffer.readPacket(750);
        packet.user = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default UserProfileNotice;