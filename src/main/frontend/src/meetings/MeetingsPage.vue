<template>
  <div>
    <div :class="'alert alert-' + (this.isError ? 'error' : 'success')" v-if="message">{{ message }}</div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>
    <span v-if="meetings.length == 0">Brak zaplanowanych spotkań.</span>
    <h3 v-else>Zaplanowane zajęcia ({{ meetings.length }})</h3>
    <meetings-list
      :meetings="meetings"
      :username="username"
      @attend="addMeetingParticipant($event)"
      @unattend="removeMeetingParticipant($event)"
      @delete="deleteMeeting($event)"
    ></meetings-list>
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";
    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: [],
                message: '',
                isError: false
            };
        },
        methods: {
            addNewMeeting(meeting) {
                this.clearMessage();
                this.$http.post('meetings', meeting)
                    .then(response => {
                        meeting.id = response.data;
                        this.meetings.push(meeting);
                        this.success('Spotkanie zostało dodane. Możesz się zapisać.');
                    })
                    .catch(response => this.failure('Błąd przy dodawaniu spotkania. Kod odpowiedzi: ' + response.status));
            },
            addMeetingParticipant(meeting) {
                var query = "meetings/" + meeting.id + "/" + this.username;
                this.$http.post(query)
                    .then( () => {
                        meeting.participants.push(this.username);
                      this.success('Pomyślnie zapisałeś sie na spotkanie.');
                    })
                     .catch(response => this.failure('Błąd przy zapisywaniu uczestnika do spotkania' + response.status));
            },
            removeMeetingParticipant(meeting) {
                var query = "meetings/" + meeting.id + "/" + this.username; 
                this.$http.delete(query)
                    .then( () => {
                        meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
                      this.success('Pomyślnie wypisałeś sie ze spotkania.');
                    })
                    .catch(response => this.failure('Błąd przy wypisywaniu uczestnika ze spotkania. Kod odpowiedzi: ' + response.status));
            },
            deleteMeeting(meeting) {
                var query = "meetings/" + meeting.id;
                this.$http.delete(query)
                    .then( () => {
                      this.meetings.splice(this.meetings.indexOf(meeting), 1);
                      this.success('Pomyślnie usunąłeś spotkanie.');
                    })
                    .catch(response => this.failure('Błąd przy usuwaniu spotkania. Kod odpowiedzi: ' + response.status));
            },
            success(message) {
                this.message = message;
                this.isError = false;
            },
            failure(message) {
                this.message = message;
                this.isError = true;
            },
            clearMessage() {
                this.message = undefined;
            },
            loadParticipants() {
              this.meetings.forEach( (meeting) => {
                var query = 'meetings/' + meeting.id + '/participants';
                this.$http.get(query)
                .then( response => 
                {
                  response.body.forEach( (participant =>
                  {
                    meeting.participants.push(participant.login);
                  }))
                })
              })
            },
            loadMeetings() {
              this.meetings = [];
              this.$http.get('meetings')
              .then( response => {
                for (var meeting of response.body){
                  meeting.participants = [];
                  this.meetings.push(meeting);
                }
                this.loadParticipants();
              })
              .catch( response => {
                this.failure('Błąd podczas pobierania listy spotkań. Kod błedu: ' + response.status);
              })
           }
        },
       mounted() {
            this.loadMeetings();            
        },
    }
</script>

<style lang="scss">
 .alert {
    padding: 10px;
    margin-bottom: 10px;
    border: 2px solid black;
    &-success {
      background: lightgreen;
      border-color: darken(lightgreen, 10%);
    }
    &-error {
      background: indianred;
      border-color: darken(indianred, 10%);
      color: white;
    }
 }
</style>