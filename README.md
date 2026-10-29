
# Student Master Project

Aplikasi desktop untuk mengelola data mahasiswa menggunakan Java Swing, MariaDB, JDBC, dan JasperReports.

## Fitur

- Menambahkan data mahasiswa (Create).
- Menampilkan daftar mahasiswa (Read).
- Mengubah data mahasiswa (Update).
- Menghapus data mahasiswa (Delete).
- Memilih foto mahasiswa.
- Mengatur warna latar yang berbeda untuk setiap mahasiswa.
- Membuat laporan mahasiswa dalam format PDF.
- Memfilter laporan berdasarkan rentang Student ID.
- Menampilkan logo pada header serta nomor halaman pada footer laporan.

## Teknologi

- Java
- Java Swing
- Maven
- MariaDB
- JDBC dan PreparedStatement
- JasperReports 7.1.0

## Persyaratan

- JDK 21 atau lebih baru.
- Maven melalui konfigurasi project.
- MariaDB atau MySQL yang kompatibel.

## Persiapan Database

1. Jalankan server MariaDB.
2. Buka phpMyAdmin.
3. Jalankan isi file `sql/student_master.sql`.
4. Pastikan database `student_master` dan tabel `students` berhasil dibuat.
5. Periksa konfigurasi koneksi pada `DatabaseConnector.java`.

Konfigurasi koneksi default:

- Host: `localhost`
- Port: `3306`
- Database: `student_master`
- Username: `root`
- Password: kosong, jika sesuai dengan konfigurasi lokal.

Sesuaikan username, password, atau port pada `DatabaseConnector.java` jika konfigurasi server berbeda.

## Menjalankan Aplikasi

1. Buka project di IntelliJ IDEA sebagai project Maven.
2. Tunggu Maven mengunduh semua dependensi.
3. Pastikan MariaDB berjalan dan database sudah dibuat.
4. Jalankan class `id.ac.budiluhur.Main`.

## Menggunakan Aplikasi

1. Isi Student ID, nama, dan email.
2. Pilih foto mahasiswa.
3. Pilih warna latar.
4. Gunakan tombol Tambah, Ubah, Hapus, dan Muat Ulang untuk mengelola data.
5. Klik Laporan PDF untuk memasukkan rentang Student ID.
6. PDF yang dihasilkan disimpan di folder `reports`.

## Struktur Project

```text
StudentMasterProject/
├── captures/
├── reports/
├── sql/
│   └── student_master.sql
├── student-photos/
├── src/main/java/
│   ├── dao/
│   ├── database/
│   ├── model/
│   ├── report/
│   ├── service/
│   ├── ui/
│   └── id/ac/budiluhur/
└── pom.xml
```

## Dokumentasi Pengujian

Folder `captures` berisi screenshot pengujian aplikasi, termasuk operasi CRUD dan hasil laporan PDF.

## Catatan

Foto mahasiswa dan laporan yang tersimpan secara lokal mungkin diperlukan agar tampilan dan hasil pengujian dapat direproduksi.
Jangan memasukkan password database atau informasi pribadi yang tidak diperlukan ke repository.
