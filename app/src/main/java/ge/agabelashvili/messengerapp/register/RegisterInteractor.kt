package ge.agabelashvili.messengerapp.register

import android.os.AsyncTask
import ge.agabelashvili.messengerapp.model.User

class RegisterInteractor(val presenter: IRegisterPresenter) {

    fun registerUserInFirebase() {
        //registerUserInFirebaseTask(presenter).execute()
    }

    /*class registerUserInFirebaseTask(val presenter: IRegisterPresenter): AsyncTask<Void, Void, User>(){
        override fun doInBackground(vararg params: Void?): User {
           *//* var moviedDao = DemoDatabase.getInstance().moviesDao()
            moviedDao.addMovie(Movie("Friends: The Reunion", 0f))
            var list = moviedDao.getMovies()
            return list*//*
        }

        override fun onPostExecute(result: User) {
            super.onPostExecute(result)
            if(result != null) {
                presenter.onUserRegistered(result)
            }
        }
    }*/

}