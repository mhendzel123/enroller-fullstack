package com.company.enroller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meeting")
public class Meeting implements Comparable<Meeting> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String date;

    @JsonIgnore
    @ManyToMany(mappedBy = "meetings")
    Set<Participant> participants = new HashSet<>();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }
//-------------------------------------------------------------------------------
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
    }

    public Collection<Participant> getParticipants() {
        return participants;
    }

//----------------------------------------------------------------------------

    public void update(Meeting updated_meeting) {
		this.setTitle(updated_meeting.getTitle());
		this.setDescription(updated_meeting.getDescription());
		this.setDate(updated_meeting.getDate());
	}

    @Override
	public int compareTo(Meeting o) {
		return this.getTitle().compareTo(o.getTitle());
	}
    
    
}
