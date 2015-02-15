package org.hejki.spring.data.jdbc.test;

import org.hejki.spring.data.jdbc.mapping.annotation.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
//@Entity
@Table(name = "nodes")
public class Node {
    @Id
    private Integer id;

//    @Column(name = "name")
    private String name;

    private String address;

    private int port;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
