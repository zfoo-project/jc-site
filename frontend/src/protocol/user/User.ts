import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class User {
    id: number = 0;
    name: string = '';
    ctime: number = 0;
    phoneNumber: number = 0;
    ask: number = 0;
    draw: number = 0;
    login: number = 0;
    cost: number = 0;
}

export class UserRegistration implements IProtocolRegistration<User> {
    protocolId(): number {
        return 750;
    }

    write(buffer: IByteBuffer, packet: User | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.ask);
        buffer.writeInt(packet.cost);
        buffer.writeLong(packet.ctime);
        buffer.writeInt(packet.draw);
        buffer.writeLong(packet.id);
        buffer.writeInt(packet.login);
        buffer.writeString(packet.name);
        buffer.writeLong(packet.phoneNumber);
    }

    read(buffer: IByteBuffer): User | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new User();
        const result0 = buffer.readInt();
        packet.ask = result0;
        const result1 = buffer.readInt();
        packet.cost = result1;
        const result2 = buffer.readLong();
        packet.ctime = result2;
        const result3 = buffer.readInt();
        packet.draw = result3;
        const result4 = buffer.readLong();
        packet.id = result4;
        const result5 = buffer.readInt();
        packet.login = result5;
        const result6 = buffer.readString();
        packet.name = result6;
        const result7 = buffer.readLong();
        packet.phoneNumber = result7;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default User;